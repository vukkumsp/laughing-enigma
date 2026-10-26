import express, { type Express } from "express";

export type DatabaseHealthCheck = () => Promise<unknown>;

export function createApp(checkDatabase: DatabaseHealthCheck): Express {
  const app = express();

  app.get("/health", async (_request, response) => {
    try {
      await checkDatabase();
      response.json({ status: "ok" });
    } catch (error) {
      console.error("Database health check failed:", error);
      response.status(503).json({ status: "unavailable" });
    }
  });

  app.use((_request, response) => {
    response.status(404).json({ error: "Not found" });
  });

  return app;
}
