import { createApp } from "./app.js";

const port = Number(8080);
const host = "0.0.0.0";

const server = createApp();

server.listen(port, host, () => {
  console.log(`Incentive service listening on port ${port}`);
});
