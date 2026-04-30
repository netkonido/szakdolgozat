import type { Route } from "./+types/home";
import {Await, Link, redirect, useLoaderData, useNavigate, useRevalidator} from "react-router";
import {TopBar} from "~/components/topBar";
import axios from "axios";
import {Suspense} from "react";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Önéletrajz Áttekintése" },
    { name: "description", content: "Ezen az oldalon megtekintheti önéletrajzának vázlatát." },
  ];
}

export async function clientLoader()
{
    await axios.get("http://localhost:8080/api/v1/session/get",{withCredentials:true}).catch(err =>{
        console.log("No valid session in progress, rerouting");
        throw redirect("/");
    });

    let resumePreview = axios.get<string>("http://localhost:8080/api/v1/session/resume-preview",{withCredentials:true})
        .then(res => res.data)
        .catch(err =>{console.log("failed to fetch resume preview: " +err.toString())});

    return { resumePreview, };
}

clientLoader.hydrate = true as const;

export default function Overview() {
    const navigate = useNavigate();
    const revalidator = useRevalidator();
    const {resumePreview,} = useLoaderData();
    return (
        <div>
            <TopBar
                title={"Önéletrajz Áttekintése"}
            />
            <div className="flex flex-col items-center bg-lime-200 pb-10">
                <div className="flex items-center justify-center p-15 w-full ">
                    <div className="h-full w-3/4 border-black border-2 bg-white overflow-scroll">
                        <Suspense fallback={<div className={"animate-pulse"}>Betöltés...</div>}>
                            <Await resolve={resumePreview}>
                                {result => <div>{result}</div>}
                            </Await>
                        </Suspense>
                    </div>
                </div>
                <div className="flex items-center bg-lime-200">
                <button type="button" className="text-black border-lime-900 border-2 rounded-xl w-100 p-3 m-5 bg-amber-300 hover:bg-amber-400 disabled:bg-gray-500 disabled:animate-pulse"
                        onClick={(e) => {
                            e.preventDefault();
                            const target= e.currentTarget;
                                target.disabled = true;
                            axios.get("http://localhost:8080/api/v1/actions/regenerate",{withCredentials:true})
                                .then(res => {
                                    revalidator.revalidate();
                                })
                                .catch(err => console.log("Regenerate request failed: "+err.toString()))
                                .finally(() => {target.disabled = false;});
                }}>
                    Újragenerálás
                </button>
                <button type="button" className="navbutton" onClick={e => {
                    e.preventDefault();
                    const target = e.currentTarget;
                    navigate("/download");
                    }}>
                    Tovább</button>
                </div>
            </div>
        </div>
        );
}