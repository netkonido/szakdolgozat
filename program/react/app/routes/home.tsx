import type { Route } from "./+types/home";
import {Link, useNavigate} from "react-router";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Resume Generator" },
    { name: "description", content: "Welcome to Resume Generator!" },
  ];
}

export default function Home() {
    const navigate = useNavigate();
    return (
          <div className="flex flex-col items-center bg-gray-400">
              <h1 className="text-5xl font-semibold p-10 text-lime-400">Kiberbiztonsági önéletrajz generátor</h1>
              <button type="button" className="navbutton" onClick={e =>{
                  e.preventDefault();
                  navigate("/data")
                }}>
                  Kezdés</button>
          </div>
          );
}
