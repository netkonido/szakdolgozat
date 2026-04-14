import type { Route } from "./+types/home";
import { Link } from "react-router";
import {TopBar} from "~/components/topBar";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Enter Job Description" },
    { name: "description", content: "This page can be used to enter the job description." },
  ];
}

export default function JobDescription() {
    return (
        <div>
            <TopBar
                linkBack={"/data"}
                title={"Álláshirdetés megadása"}
            />
            <div className="flex flex-col items-center bg-gray-400">
                <textarea name="jobDescription" placeholder="Álláshirdetés megadása" className="border-black border-2 rounded-md w-1/2 bg-white m-10 h-30"></textarea>
                <Link to="/overview">
                    <button type="button" className="navbutton">
                        Tovább</button></Link>
            </div>
        </div>
        );
}