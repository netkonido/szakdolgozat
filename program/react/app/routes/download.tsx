import type { Route } from "./+types/home";
import { Link } from "react-router";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Download Resume" },
    { name: "description", content: "This page can be used to download the completed resume." },
  ];
}

export default function Download() {
    return (
        <main>
            <h1>Download resume</h1>
            <Link to="/end-session">End Session</Link>
        </main>
        );
}