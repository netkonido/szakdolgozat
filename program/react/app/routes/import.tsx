import {Await, Form, redirect, useLoaderData, useNavigate, useRevalidator} from "react-router";
import {TopBar} from "~/components/topBar";
import axios from "axios";
import type {Route} from "../../../.react-router/types/app/+types/root";
import React, {Suspense, useState} from "react";

export function meta({}: Route.MetaArgs) {
    return [
        {title: "Adatok Importálása"},
        {name: "description", content: "Ezen az oldalon importálhat adatokat."},
    ];
}

type FileMetadata = {
    id: number,
    originalName: string,
    size: number,
}

export async function clientLoader() {
    let res = await axios.get("http://localhost:8080/api/v1/session/get", {withCredentials: true})
        .then(res => res.data)
        .catch(err => {
            if(err.status === 400){
                console.log("No valid session in progress, rerouting");
                throw redirect("/");
            }
    });
    const files = axios.get<FileMetadata[]>("http://localhost:8080/api/v1/files/uploaded-files", {withCredentials: true})
        .then(res => res.data)
        .catch(err => {
        console.log("failed to get uploaded files: " + err.toString());
        return[];
    })

    return {files, res};

}

clientLoader.hydrate = true as const;

export function FileListItem({metadata,isDisabled}: {metadata: FileMetadata, isDisabled: boolean}) {
    const revalidator = useRevalidator();
    return (
        <div className="flex w-full p-2 align-middle items-center justify-center">
            <button disabled={isDisabled} className="w-1/5 bg-amber-500 hover:bg-amber-600 border border-lime-900 rounded-md h-10 mr-2 disabled:bg-gray-500"
                    onClick={e => {
                        e.preventDefault();
                        axios.delete("http://localhost:8080/api/v1/files/delete", {
                            withCredentials: true,
                            headers: {"Content-Type": "multipart/form-data"},
                            data: {"id": metadata.id}
                        })
                            .then(res => revalidator.revalidate())
                            .catch(err => {
                                console.log("Could not delete file: " + err.toString())
                            });
                    }}>Törlés
            </button>
            <p className="w-4/5 truncate text-wrap h-full">{metadata.originalName}</p>

        </div>
    )
}

function linkedInField()
{
    const [linkValue, setLinkValue] = useState<string>();
    return (<><div className="flex-3 m-10 p-5">
        <h2 className="text-2xl font-bold p-3 text-center ">LinkedIn profil oldalának feltöltése</h2>
        <input type={"file"} id="linkedIn" value={linkValue} placeholder={"linkedin.com/in/..."}
               className="p-2 bg-white rounded-md hover:bg-gray-200 w-full border-2 border-lime-900"
               onChange={e => {
                   setLinkValue(e.target.value);
                   //setExtractError(false)
               }}
        />
    </div>
    <div className="flex-1 m-10 p-5 items-center">
        <h2 className="text-5xl font-bold text-center">Vagy</h2>
    </div></>);
}

export default function Import() {
    const navigate = useNavigate();
    const revalidator = useRevalidator();
    let {files, res} = useLoaderData();
    const [fileCount, setFileCount] = useState(0);
    const [continueButtonText, setContinueButtonText] = useState<string>("Tovább");
    const [isExtractError, setExtractError] = useState<boolean>(false);
    const [isLoading, setIsLoading] = useState<boolean>(false);

    return (
        <div>
            <TopBar
                title={"Adatok Importálása"}
            />
            <div className="flex flex-col items-center bg-lime-200">
                <div className="flex items-center justify-items-center w-full p-15">
                    <div
                        className="flex-3 flex-col m-10 p-5 border-4 border-dashed rounded-2xl border-lime-900 w-100 min-h-60 justify-center content-evenly">
                        <h2 className={"text-2xl font-bold p-3 text-center"}>File feltöltése</h2>
                        <div className="flex flex-col align-middle">
                            <Suspense fallback={<div className={"animate-pulse"}>Betöltés...</div>}>
                                <Await resolve={files}>
                                    {result => {
                                        setFileCount(result.length);
                                        return result.map((md: FileMetadata) => <FileListItem isDisabled={isLoading} metadata={md} key={md.id} />);
                                    }}
                                </Await>
                            </Suspense>

                        </div>
                        <Form className="flex justify-center items-center text-center align-middle" onSubmit={e => {
                            e.preventDefault();
                            const inputFile = e.currentTarget.elements.namedItem("fileInput") as HTMLInputElement;
                            const formData = {
                                file: inputFile.files![0],
                            }
                            axios.post("http://localhost:8080/api/v1/files/upload", formData, {withCredentials: true, headers: {"Content-Type": "multipart/form-data"}})
                                .then(res => {
                                revalidator.revalidate();
                                })
                                .catch(err => {
                                    console.log("could not upload file" + err.toString())
                                })
                            e.currentTarget.reset();

                        }}>
                            <input type="file" id="fileInput" required={true} disabled={fileCount>=10 || isLoading}
                                   accept="application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,text/markdown,text/html"
                                   placeholder={"File Kiválasztása"}
                                   className="min-w-30 h-10 bg-white hover:bg-gray-200 disabled:bg-gray-500 rounded-l-md border-2 border-r-0 border-lime-900 w-2/3 items-center"/>
                            <button type="submit" id="submit" disabled={fileCount >= 10 || isLoading}
                                    className="h-10 border-2 border-lime-900 rounded-r-md w-1/3 text-nowrap min-w-fit bg-lime-400 hover:bg-lime-500 disabled:bg-gray-500">Feltöltés {fileCount}/10
                            </button>
                        </Form>
                    </div>
                </div>
                <h2 className="text-red-600 font-bold text-xl bg-yellow-300 rounded-xl text-center m-1" hidden={!isExtractError} >Hiba történt a fileok feldolgozása során!</h2>
                <button type="button" className="navbutton" disabled={isLoading} onClick={e => {
                    e.preventDefault();
                    setExtractError(false);
                    setIsLoading(true);
                    setContinueButtonText("Betöltés...")
                    const linkValue = "";
                    axios.post("http://localhost:8080/api/v1/actions/import-data", {"link":linkValue}, {withCredentials: true, headers:{"Content-Type":"multipart/form-data"}})
                        .then(res => {
                            navigate("/data");})
                        .catch(err => {
                            console.log("could not extract link" + err.toString());
                            setExtractError(true);})
                        .finally(() => {
                            setContinueButtonText("Tovább");
                            setIsLoading(false);
                        })
                }}>
                    {continueButtonText}
                </button>
            </div>
        </div>
    );
}