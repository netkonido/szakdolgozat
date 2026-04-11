import type { Route } from "./+types/home";
import { Link } from "react-router";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Resume Generator" },
    { name: "description", content: "Welcome to Resume Generator!" },
  ];
}

export default function Home() {
      return (
          <main>
              <h1>Resume Generator</h1>
              <Link to="/data">Data Entry</Link>
          </main>
          );
}
