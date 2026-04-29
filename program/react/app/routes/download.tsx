import type { Route } from "./+types/home";
import {Form, Link, redirect, useNavigate} from "react-router";
import {TopBar} from "~/components/topBar";
import axios from "axios";
import {useState} from "react";

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
    const [downloadDisabled, setDownloadDisabled] = useState(false);
    return (
        <div>
            <TopBar
                title={"Önéletrajz letöltése"}
            />
            <div className="flex flex-col items-center bg-lime-200">
                <h2 className="text-2xl text-black p-5">Formátum kiválasztása</h2>
                <Form onSubmit={e => {
                    e.preventDefault();
                    setDownloadDisabled(true);
                    const select = e.currentTarget.elements.namedItem("fileType") as HTMLFormElement
                    axios.get(`http://localhost:8080/api/v1/files/download/${select.value}`, {withCredentials:true})
                        .catch(err =>{console.log("Could not download file: "+ err.toString())})
                        .finally(()=> {
                            setDownloadDisabled(false);
                        })
                }
                }>
                    <select id="fileType" className="border-black border-2 rounded-md w-40 bg-white m-5 p-2">
                        <option value={"pdf"}>PDF</option>
                        <option value={"docx"}>Docx</option>
                    </select>
                    <button type="submit" className="navbutton" disabled={downloadDisabled}>Letöltés</button>
                </Form>
            </div>
        </div>
        );
}