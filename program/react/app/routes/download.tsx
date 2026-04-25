import type { Route } from "./+types/home";
import {Link, redirect, useNavigate} from "react-router";
import {TopBar} from "~/components/topBar";
import axios from "axios";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Download Resume" },
    { name: "description", content: "This page can be used to download the completed resume." },
  ];
}

export async function clientLoader()
{
    await axios.get("http://localhost:8080/api/v1/session/get",{withCredentials:true}).catch(err =>{
        console.log("No valid session in progress, rerouting");
        throw redirect("/");
    });
}

clientLoader.hydrate = true as const;

export default function Download() {
    const navigate = useNavigate();
    return (
        <div>
            <TopBar
                title={"Önéletrajz letöltése"}
            />
            <div className="flex flex-col items-center bg-gray-400">
                <h2 className="text-2xl text-black p-5">Formátum kiválasztása</h2>
                <select className="border-black border-2 rounded-md w-40 bg-white m-5 p-2">
                    <option value={"PDF"}>PDF</option>
                    <option value={"Docx"}>Docx</option>
                </select>
                <button type="button" className="navbutton" onClick={e => {
                    e.preventDefault();
                    // send request
                    navigate("/");}}>
                    Letöltés</button>
            </div>
        </div>
        );
}