import {
    type RouteConfig,
    route,
    prefix,
    index
     } from "@react-router/dev/routes";

export default [
    index("routes/home.tsx"),
    route("/data", "./data/data.tsx"),
    route("/data/import", "./data/import.tsx"),
    route("/job","./routes/job.tsx"),
    route("/overview", "./routes/overview.tsx"),
    route("/download", "./routes/download.tsx"),
    route("/end-session", "./routes/end_session.tsx")
    ] satisfies RouteConfig;
