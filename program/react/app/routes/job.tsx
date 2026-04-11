import type { Route } from "./+types/home";
import { Link } from "react-router";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Enter Job Description" },
    { name: "description", content: "This page can be used to enter the job description." },
  ];
}

export default function JobDescription() {
    return (
        <main>
            <h1>Enter Job Description</h1>
            <ul>
                <li><Link to="/overview">Overview</Link></li>
                <li><Link to="/end-session">End Session</Link></li>
            </ul>
        </main>
        );
}