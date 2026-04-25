import type { Route } from "./+types/home";
import {Link, redirect, useNavigate} from "react-router";
import axios from "axios";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "End Session" },
    { name: "description", content: "This page can be used to end the current session and delete all user data." },
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

export default function EndSession() {
    const navigate = useNavigate();
    return (
        <div className="flex-col items-center  bg-lime-300 p-5">
            <h1 className="text-7xl text-black font-semibold p-5 text-center">Munkamenet megszakítása</h1>
            <div className="flex justify-center p-10">
                <button className="navbutton" onClick={e =>{e.preventDefault()
                navigate(-1)}}>Mégse</button>
                <button type="button" className="text-white border-lime-900 p-5 m-5 border-2 rounded-xl w-100 font-bold text-nowrap bg-red-600 hover:bg-red-700" onClick={e=>{
                    e.preventDefault()
                    axios.delete("http://localhost:8080/api/v1/session/end",{withCredentials:true}).then(res=> {
                        console.log("Deleted session.");
                        navigate("/");
                    }).catch(err => console.log("error ending session" + err.toString()));
                }}>Megszakítás és visszatérés a kezdőlapra</button>
            </div>
        </div>
        );
}