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
          <div className="flex flex-col items-center bg-gray-400">
              <h1 className="text-5xl font-semibold p-10 text-lime-400">Kiberbiztonsági önéletrajz generátor</h1>
              <ul>
                  <li><Link to="/data">
                      <button type="button" className="navbutton">
                          Kezdés</button>
                  </Link></li>
              </ul>
          </div>
          );
}
