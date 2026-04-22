import type { Route } from "./+types/home";
import {Link, useNavigate} from "react-router";
import {TopBar} from "~/components/topBar";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Resume Overview" },
    { name: "description", content: "This page shows the user their resume draft." },
  ];
}

export default function Overview() {
    const navigate = useNavigate();
    return (
        <div>
            <TopBar
                title={"Önéletrajz áttekintése"}
            />
            <div className="flex flex-col items-center bg-gray-400 pb-10">
                <p className="text-xl p-5">Resumme preview goes here</p>
                <div className="flex items-center p-15 bg-gray-400">
                    <div className="h-200 w-150 border-black border-2 bg-white">
                        <p className="text-8xl">lorem ipsum dolor sit amet</p>
                    </div>
                </div>
                <div className="flex items-center bg-gray-400">
                <button type="button" className="text-black border-black border-2 rounded-md w-80 p-5 m-5 bg-amber-300 hover:bg-amber-400" onClick={() => navigate("/overview")}>
                    Újragenerálás
                </button>
                <button type="button" className="navbutton" onClick={e => {
                    e.preventDefault();
                    // send request
                    navigate("/download");}}>
                    Tovább</button>
                </div>
            </div>
        </div>
        );
}