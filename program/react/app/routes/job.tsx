import type { Route } from "./+types/home";
import {Link, redirect, useNavigate} from "react-router";
import {TopBar} from "~/components/topBar";
import axios from "axios";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Enter Job Description" },
    { name: "description", content: "This page can be used to enter the job description." },
  ];
}

export async function clientLoader()
{
    await axios.get("http://localhost:8080/api/v1/session/get",{withCredentials:true}).catch(err =>{
        console.log("No valid session in progress, rerouting");
        throw redirect("/");
    });

    const result = await axios.get('http://localhost:8080/api/v1/data/job-description', {withCredentials: true}).then(res => res.data).catch(err => err);
    console.log(result.toString());
    if (result.toString() === "")
    {
        await axios.post('http://localhost:8080/api/v1/data/job-description',{content:"",}, {withCredentials: true, headers:{"Content-Type": "multipart/form-data"}}).then(res => res.data).catch(err => err);
    }

}

clientLoader.hydrate = true as const;

export default function JobDescription() {
    const navigate = useNavigate();
    return (
        <div>
            <TopBar
                title={"Álláshirdetés megadása"}
            />
            <div className="flex flex-col items-center bg-gray-400">
                <textarea name="jobDescription" placeholder="Álláshirdetés megadása" className="border-black border-2 rounded-md w-1/2 bg-white m-10 h-30"></textarea>
                <button type="button" className="navbutton" onClick={e => {
                    e.preventDefault();
                    // send request
                    navigate("/overview");}}>
                    Tovább</button>
            </div>
        </div>
        );
}