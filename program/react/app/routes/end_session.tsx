import type { Route } from "./+types/home";
import {Link, useNavigate} from "react-router";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "End Session" },
    { name: "description", content: "This page can be used to end the current session and delete all user data." },
  ];
}

export default function EndSession() {
    const navigate = useNavigate();
    return (
        <div className="flex-col items-center  bg-gray-600 p-5">
            <h1 className="text-7xl text-black font-semibold p-10 text-center">Munkamenet megszakítása</h1>
            <div className="flex justify-center p-10">
                <button className="navbutton" onClick={e =>{e.preventDefault()
                navigate(-1)}}>Mégse</button>
                <button type="button" className="text-black border-black p-5 m-5 border-2 rounded-md w-80 bg-red-600 hover:bg-red-700" onClick={e=>{
                    e.preventDefault()
                    //effect: delete all session files
                    navigate("/");
                }}>Megszakítás és visszatérés a kezdőlapra</button>
            </div>
        </div>
        );
}