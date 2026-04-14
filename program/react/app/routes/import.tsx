import type { Route } from "./+types/home";
import { Link } from "react-router";
import {TopBar} from "~/components/topBar";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Import Data" },
    { name: "description", content: "This page can be used to import data" },
  ];
}

export default function Import() {
    return (
        <div>
            <TopBar
            linkBack={"/data"}
            title={"Adatok Importálása"}
            />
            <div className="flex flex-col items-center bg-gray-400">
                <h1 className="text-lime-400 text-5xl p-5 font-semibold">Adatok importálása</h1>
                <Link to="/data">
                    <button type="button" className="navbutton">
                        Tovább</button></Link>
                <Link to="/end-session">
                    <button type="button" className="endsession">
                        Munkamenet megszakítása</button></Link>
            </div>
        </div>
        );
}