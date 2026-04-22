import type {Route} from "../+types/root";
import {Link, useNavigate} from "react-router";

interface Props {
    title: string
}

export function TopBar({
                            title,
                       }: Props) {
    const navigate = useNavigate();
    return (
        <div className="flex border-lime-950 top-0 bg-lime-700 h-fit w-full">
            <div className="flex-1 content-center justify-items-start">
                <button type="button" className="backbutton" onClick={e=>{
                    e.preventDefault();
                    navigate(-1);
                }}>Vissza
                </button>
            </div>
            <div className="flex-1 content-center justify-items-center">
                <h1 className=" text-black text-4xl p-5 font-semibold">{title}</h1>
            </div>
            <div className="flex-1 content-center justify-items-end text-right">
                <button type="button" className="endsession" onClick={e=>{
                    e.preventDefault()
                    navigate("/end-session");
                }}>Munkamenet megszakítása
                </button>
            </div>
        </div>
    )
}