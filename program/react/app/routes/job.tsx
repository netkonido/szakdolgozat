import type { Route } from "./+types/home";
import {useLoaderData, redirect, useNavigate, useRevalidator} from "react-router";
import {TopBar} from "~/components/topBar";
import axios from "axios";
import {useState} from "react";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Álláshirdetés Megadása" },
    { name: "description", content: "Ezen az oldalon megadhatja az álláshirdetést." },
  ];
}

export async function clientLoader()
{
    await axios.get("http://localhost:8080/api/v1/session/get",{withCredentials:true}).catch(err =>{
        console.log("No valid session in progress, rerouting");
        throw redirect("/");
    });

    let jobDescription = await axios.get('http://localhost:8080/api/v1/data/job-description', {withCredentials: true}).then(res => res.data).catch(err => console.log("Could not fetch job description: " + err.toString()));
    if (jobDescription.toString() === "")
    {
        jobDescription = await axios.post('http://localhost:8080/api/v1/data/job-description',{content:"",}, {withCredentials: true, headers:{"Content-Type": "multipart/form-data"}}).then(res => res.data).catch(err => err);
    }
    return {jobDescription,};
}

clientLoader.hydrate = true as const;

export default function JobDescription() {
    const navigate = useNavigate();
    const revalidator = useRevalidator();
    const {jobDescription,} = useLoaderData();
    const [jobDescriptionFieldValue, setJobDescriptionFieldValue] = useState(jobDescription.content??"");
    const [isLoading, setIsLoading] = useState(false);
    return (
        <div>
            <TopBar
                title={"Álláshirdetés Megadása"}
            />
            <div className="flex flex-col items-center bg-lime-200">
                <textarea disabled={isLoading} name="jobDescription" placeholder="Álláshirdetés megadása" value={jobDescriptionFieldValue}
                          onChange={(e) => {
                              e.preventDefault();
                              setJobDescriptionFieldValue(e.target.value);
                          }}
                          onBlur={(e) => {
                              e.preventDefault();
                              axios.patch(`http://localhost:8080/api/v1/data/job-description`, {"content":jobDescriptionFieldValue}, {withCredentials:true, headers:{"Content-Type":"multipart/form-data"}})
                                  .then(res => {
                                      revalidator.revalidate();
                                  })
                                  .catch(err => console.log(err));
                          }}
                          className="border-black border-2 rounded-md w-1/2 bg-white hover:bg-gray-200 m-10 h-30"/>
                <button type="button" disabled={isLoading} className="navbutton" onClick={e => {
                    e.preventDefault();
                    setIsLoading(true);
                    axios.patch(`http://localhost:8080/api/v1/data/job-description`, {"content":jobDescriptionFieldValue}, {withCredentials:true, headers:{"Content-Type":"multipart/form-data"}})
                        .then(res =>{
                            axios.get("http://localhost:8080/api/v1/actions/prepare-resume",{withCredentials:true})
                                .then(res => {
                                    setIsLoading(false);
                                    navigate("/overview");
                            })
                            .catch(err => console.log("Request failed: "+err.toString()))
                                .finally(()=> setIsLoading(false));
                            })
                }}>

                    Generálás</button>
            </div>
        </div>
        );
}