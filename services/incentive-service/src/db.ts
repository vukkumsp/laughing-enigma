import { readFileSync } from "node:fs";
import { Pool } from "pg";
import type { PoolConfig } from "pg";

function requiredEnvironmentVariable(name: string): string {
  const value = process.env[name]?.trim();
  if (!value) {
    throw new Error(`Missing required environment variable: ${name}`);
  }
  return value;
}

function databasePort(): number {
  const value = process.env.DB_PORT ?? "5432";
  const port = Number(value);
  if (!Number.isInteger(port) || port < 1 || port > 65535) {
    throw new Error("DB_PORT must be an integer between 1 and 65535");
  }
  return port;
}

function databaseSsl(): PoolConfig["ssl"] {
  const sslSetting = process.env.DB_SSL?.trim().toLowerCase() || "false";
  if (sslSetting !== "true" && sslSetting !== "false") {
    throw new Error("DB_SSL must be either true or false");
  }

  const caFile = process.env.DB_SSL_CA?.trim();
  if (sslSetting === "false") {
    if (caFile) {
      throw new Error("DB_SSL_CA can only be set when DB_SSL=true");
    }
    return false;
  }

  return {
    rejectUnauthorized: true,
    ...(caFile ? { ca: readFileSync(caFile, "utf8") } : {}),
  };
}

export const pool = new Pool({
  host: process.env.DB_HOST?.trim() || "localhost",
  port: databasePort(),
  database: requiredEnvironmentVariable("DB_NAME"),
  user: requiredEnvironmentVariable("DB_USER"),
  password: requiredEnvironmentVariable("DB_PASSWORD"),
  // ssl: databaseSsl(),
  ssl: {
    rejectUnauthorized: false
  }
});

export async function checkDatabase(): Promise<void> {
  await pool.query("SELECT 1");
}
