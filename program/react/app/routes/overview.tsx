import type { Route } from "./+types/home";
import { Link } from "react-router";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Resume Overview" },
    { name: "description", content: "This page shows the user their resume draft." },
  ];
}

export default function Overview() {
    return (
        <main>
            <h1>Resume Overview</h1>
            <ul>
                <li><Link to="/download">Download</Link></li>
                <li><Link to="/end-session">End Session</Link></li>
            </ul>
        </main>
        );
}