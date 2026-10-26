import assert from "node:assert/strict";
import http from "node:http";
import type { Server } from "node:http";
import { once } from "node:events";
import test from "node:test";
import { createApp } from "../app.js";

async function startServer(): Promise<Server> {
  const server = createApp().listen(0, "127.0.0.1");
  await once(server, "listening");
  return server;
}

function getResponse(
  server: Server,
  path: string,
): Promise<{ statusCode: number | undefined; body: string }> {
  const address = server.address();
  if (!address || typeof address === "string") {
    throw new Error("Expected the test server to listen on a TCP port");
  }

  return new Promise((resolve, reject) => {
    http
      .get(`http://127.0.0.1:${address.port}${path}`, (response) => {
        let body = "";
        response.setEncoding("utf8");
        response.on("data", (chunk: string) => {
          body += chunk;
        });
        response.on("end", () => resolve({ statusCode: response.statusCode, body }));
      })
      .on("error", reject);
  });
}

test("GET /health returns the service health status", async () => {
  const server = await startServer();

  try {
    const response = await getResponse(server, "/health");
    assert.equal(response.statusCode, 200);
    assert.deepEqual(JSON.parse(response.body), { status: "ok" });
  } finally {
    server.close();
    await once(server, "close");
  }
});

test("unknown routes return a JSON 404", async () => {
  const server = await startServer();

  try {
    const response = await getResponse(server, "/missing");
    assert.equal(response.statusCode, 404);
    assert.deepEqual(JSON.parse(response.body), { error: "Not found" });
  } finally {
    server.close();
    await once(server, "close");
  }
});
