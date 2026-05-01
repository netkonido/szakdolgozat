import type {Route} from "../+types/root";
import {Link, useNavigate} from "react-router";

interface Props {
    title: string,
}

export function TopBar({
                            title,
                       }: Props) {
    const navigate = useNavigate();
    return (
        <div className="flex border-lime-950 top-0 bg-lime-500 h-fit w-full">
            <div className="flex-1 content-center items-baseline">
                <button type="button" className="text-black font-bold border-lime-900 p-2 m-5 border-2 rounded-xl bg-amber-500 hover:bg-amber-600" onClick={e=>{
                    e.preventDefault();
                    navigate(-1);
                }}>Vissza
                </button>
            </div>
            <div className="flex-1 content-center justify-items-center">
                <h1 className=" text-black text-center text-nowrap text-4xl p-5 font-semibold">{title}</h1>
            </div>
            <div className="flex-1 content-center justify-items-end text-right">
                <button type="button" className="text-white font-bold border-lime-900 p-2 m-5 border-2 rounded-xl bg-red-600 hover:bg-red-700" onClick={e=>{
                    e.preventDefault()
                    navigate("/end-session");
                }}>Munkamenet Megszakítása
                </button>
            </div>
        </div>
    )
}