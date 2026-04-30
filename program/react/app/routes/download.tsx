import type { Route } from "./+types/home";
import {Form, Link, redirect, useLoaderData, useNavigate} from "react-router";
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

    const types = await axios.get<string[]>("http://localhost:8080/api/v1/files/available-file-types", {withCredentials:true})
        .then(res => res.data)
        .catch(err => {console.log(err); return [];});

    return {types,};
}

clientLoader.hydrate = true as const;

type selectItemsArgs = {
    fileType : string,
}

export default function Download() {
    const navigate = useNavigate();
    const [downloadDisabled, setDownloadDisabled] = useState(false);
    const [selectedValue, setSelectedValue] = useState<string>();
    let {types,} = useLoaderData();
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
                    const url = `http://localhost:8080/api/v1/files/download-resume/${select.value}`;
                    const link = document.createElement('a');
                    link.target = '_blank';
                    link.href = url;
                    link.download = 'preferred-filename.file-extension';
                    link.click();
                    setDownloadDisabled(false);
                }
                }>
                    <select id="fileType" className="border-black border-2 rounded-md w-40 bg-white m-5 p-2">
                        {types.map((element : string)=><option key={element} value={element}>{element.toUpperCase()}</option>)}
                    </select>
                    <button type="submit" className="navbutton" disabled={downloadDisabled}>Letöltés</button>
                </Form>
            </div>
        </div>
        );
}