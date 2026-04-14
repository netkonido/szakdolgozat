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
        <div className="flex flex-col items-center">
            <h1>Download resume</h1>
            <ul>
                <li><Link to="/end-session">End Session</Link></li>
            </ul>

        </div>
        );
}