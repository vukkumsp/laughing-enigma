import "dotenv/config";
import { createApp } from "./app.js";
import { checkDatabase, pool } from "./db.js";

const port = Number(process.env.PORT || 8080);
const host = process.env.HOST || "0.0.0.0";

if (!Number.isInteger(port) || port < 1 || port > 65535) {
  throw new Error("PORT must be an integer between 1 and 65535");
}

const server = createApp(checkDatabase);

const httpServer = server.listen(port, host, () => {
  console.log(`Incentive service listening on port ${port}`);
});

for (const signal of ["SIGINT", "SIGTERM"] as const) {
  process.on(signal, () => {
    httpServer.close((error) => {
      if (error) {
        console.error("Failed to close HTTP server:", error);
        process.exitCode = 1;
      }

      void pool.end().catch((poolError: unknown) => {
        console.error("Failed to close PostgreSQL pool:", poolError);
        process.exitCode = 1;
      });
    });
  });
}
