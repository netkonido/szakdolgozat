import type { Route } from "./+types/home";
import { Link } from "react-router";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Import Data" },
    { name: "description", content: "This page can be used to import data" },
  ];
}

export default function Import() {
    return (
        <main>
            <h1>Import Data</h1>
            <ul>
                <li><Link to="/data">Go Back</Link></li>
                <li><Link to="/end-session">End Session</Link></li>
            </ul>
        </main>
        );
}