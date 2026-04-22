import type { Route } from "../../.react-router/types/app/routes/+types";
import {Link, useNavigate} from "react-router";
import {TopBar} from "~/components/topBar";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Import Data" },
    { name: "description", content: "This page can be used to import data" },
  ];
}

export default function Import() {
    const navigate = useNavigate();
    return (
        <div>
            <TopBar
            title={"Adatok Importálása"}
            />
            <div className="flex flex-col items-center bg-gray-400">
                <div className="flex items-center">
                    <div className="flex-1 m-10 p-5 border-2 border-black">
                        <h2>LinkedIn profil hivatkozás megadása</h2>
                        <input type={"text"} placeholder={"LinkedIn hivatkozás"} className={"p-2 bg-white rounded-md border-2 border-black"}/>
                    </div>
                    <div className="flex-1 m-10 p-5 border-2 border-black w-50 items-center">
                        <h2 className="text-3xl text-center">Vagy</h2>
                    </div>
                    <div className="flex-1 m-10 p-5 border-2 border-dashed rounded-xl border-black items-center w-100 h-60">
                        <h2 className={"text-center text-xl pb-3"}>File feltöltése</h2>
                        <input type={"file"} className={"p-2 bg-white rounded-md border-2 border-black"}/>
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