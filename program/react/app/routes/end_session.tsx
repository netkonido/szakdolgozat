import type { Route } from "./+types/home";
import { Link } from "react-router";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "End Session" },
    { name: "description", content: "This page can be used to end the current session and delete all user data." },
  ];
}

export default function EndSession() {
    return (
        <main>
            <h1>End session</h1>
            <Link to="/">Back to the beginning</Link>
        </main>
        );
}