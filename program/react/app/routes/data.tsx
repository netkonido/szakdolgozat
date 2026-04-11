import type { Route } from "./+types/home";
import { Link } from "react-router";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Data Entry" },
    { name: "description", content: "This page can be used to enter personal data." },
  ];
}

export default function Data() {
    return (
        <main>
            <h1>Data entry</h1>
            <ul>
                <li><Link to="/data/import">Import Data</Link></li>
                <li><Link to="/job">Enter Job Description</Link></li>
                <li><Link to="/end-session">End Session</Link></li>
            </ul>
        </main>
        );
}