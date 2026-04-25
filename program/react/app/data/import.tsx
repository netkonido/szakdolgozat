import {redirect, useNavigate} from "react-router";
import {TopBar} from "~/components/topBar";
import axios from "axios";
import type { Route } from "../+types/root";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Import Data" },
    { name: "description", content: "This page can be used to import data" },
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

export default function Import() {
    const navigate = useNavigate();
    return (
        <div>
            <TopBar
            title={"Adatok Importálása"}
            />
            <div className="flex flex-col items-center bg-lime-200">
                <div className="flex items-center justify-items-center w-full p-15">
                    <div className="flex-1 m-10 p-5 border border-black">
                        <h2 className="text-xl text-nowrap ">LinkedIn profil hivatkozás megadása</h2>
                        <input type={"text"} id="linkedIn" placeholder={"LinkedIn hivatkozás"} className="p-2 bg-white rounded-md hover:bg-gray-200 w-3/4 border-2 border-lime-900"/>
                    </div>
                    <div className="flex-1 m-10 p-5 items-center">
                        <h2 className="text-5xl font-bold text-center">Vagy</h2>
                    </div>
                    <div className="flex-1 m-10 p-5 border-4 border-dashed rounded-2xl border-lime-900 w-100 h-60 justify-items-center">
                        <h2 className={"text-2xl font-bold pb-3 text-center"}>File feltöltése</h2>
                        <input type="text" className="p-2 bg-white hover:bg-gray-200 rounded-md border-2 border-lime-900 w-full mt-10"/>
                    </div>
                </div>
                <button type="button" className="navbutton" onClick={e=>{
                    e.preventDefault();
                    navigate("/data")}}>
                    Tovább</button>
            </div>
        </div>
        );
}