import {Form, redirect, useLoaderData, useNavigate} from "react-router";
import {TopBar} from "~/components/topBar";
import type {Route} from "../../../.react-router/types/app/+types/root";
import DataItemList from "~/components/data_item_list";
import axios from "axios";
import {useState} from "react";

type Item = { id: number; content: string };

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Adatok Bevitele" },
    { name: "description", content: "Ezen az oldalon felviheti az adatait" },
  ];
}

export async function clientLoader(args:Route.ClientLoaderArgs)
{
    await axios.get("http://localhost:8080/api/v1/session/get",{withCredentials:true}).catch(err =>{
        console.log("No valid session in progress, rerouting");
        throw redirect("/");
    });

    let userData = await axios.get('http://localhost:8080/api/v1/data/user-data', {withCredentials: true})
        .then(res => res.data)
        .catch(err => console.log("Error fetching user data: " + err.toString()));
    if (userData.toString() === "")
    {
        userData = await axios.post('http://localhost:8080/api/v1/data/user-data',{name:"", email:"", telephone:"",}, {withCredentials: true, headers:{"Content-Type": "multipart/form-data"}})
            .then(res => res.data)
            .catch(err => err);
    }

    let certificationsPromise = axios.get<Item[]>(`http://localhost:8080/api/v1/data/certifications`,{withCredentials: true}).then(res => res.data).catch(res => console.log(res));
    let educationsPromise = axios.get<Item[]>(`http://localhost:8080/api/v1/data/educations`,{withCredentials: true}).then(res => res.data).catch(res => console.log(res));
    let workExperiencesPromise = axios.get<Item[]>(`http://localhost:8080/api/v1/data/work-experiences`,{withCredentials: true}).then(res => res.data).catch(res => console.log(res));
    let otherFieldsPromise = axios.get<Item[]>(`http://localhost:8080/api/v1/data/other-fields`,{withCredentials: true}).then(res => res.data).catch(res => console.log(res));

    return { certificationsPromise, educationsPromise, workExperiencesPromise, otherFieldsPromise, userData};
}
clientLoader.hydrate = true as const;

export default function Data({loaderData,}:Route.ComponentProps) {
    const navigate = useNavigate();
    let { certificationsPromise, educationsPromise, workExperiencesPromise, otherFieldsPromise, userData } = useLoaderData();
    const [nameFieldValue, setNameFieldValue] = useState<string>(userData.name??"");
    const [emailFieldValue, setEmailFieldValue] = useState<string>(userData.emailAddress?? "");
    const [telFieldValue, setTelFieldValue] = useState<string>(userData.telephoneNumber?? "");
    return (
        <div>
            <TopBar
                title={"Adatok Bevitele"}
            />
            <div className="flex flex-col items-center bg-lime-200 p-5">
                <button type="button" className="navbutton" onClick={e => {e.preventDefault()
                navigate("/data/import")}}>
                    Adatok importálása</button>

                <div className="flex w-full">
                    <Form
                        onBlur={e => {
                            e.preventDefault();
                            const formData = {
                                name: nameFieldValue,
                                email:emailFieldValue,
                                telephone:telFieldValue,
                            }
                            axios.patch('http://localhost:8080/api/v1/data/user-data', formData ,{withCredentials: true, headers:{"Content-Type":"multipart/form-data"}}).then(res => null).catch(err => null);
                        }}>
                        <div className="flex-1/3 items-center p-5 m-5">
                            <h2 className="text-black text-3xl font-semibold p-3">Alapadatok bevitele</h2>
                            <div className="m-3">
                                <h3 className="text-xl text-black p-2">Név</h3>
                                <input type="text" placeholder="Minta János" value={nameFieldValue}
                                       onChange={e=>{
                                           e.preventDefault();
                                           setNameFieldValue(e.target.value)
                                       }}
                                       className="bg-white hover:bg-gray-200 h-10 w-full border-2 border-black rounded-md p-2"/>
                            </div>
                            <div className="m-3">
                                <h3 className="text-xl text-black p-2">Email cím</h3>
                                <input type="email" placeholder="janos.minta@levelezes.hu" value={emailFieldValue}
                                       onChange={e=>{
                                           e.preventDefault();
                                           setEmailFieldValue(e.target.value);
                                       }}
                                       className="bg-white hover:bg-gray-200 h-10 w-full border-2 border-black rounded-md p-2"/>
                            </div>
                            <div className="m-3">
                                <h3 className="text-xl text-black p-2">Telefonszám</h3>
                                <input type="tel" placeholder="+36301234567" value={telFieldValue}
                                       onChange={e=>{
                                           e.preventDefault();
                                           setTelFieldValue(e.target.value);
                                       }}
                                       className="bg-white hover:bg-gray-200 h-10 w-full border-2 border-black rounded-md p-2"/>
                            </div>
                        </div>
                    </Form>
                    <div className="flex-col flex-2/3 items-center border-l-3  border-lime-900">
                        <DataItemList name={"Tanulmányok"} apiEndpoint={"/educations"} items={educationsPromise}/>
                        <DataItemList name={"Képzettségek"} apiEndpoint={"/certifications"} items={certificationsPromise}/>
                        <DataItemList name={"Munkatapasztalatok"} apiEndpoint={"/work-experiences"} items={workExperiencesPromise}/>
                        <DataItemList name={"Egyebek"} apiEndpoint={"/other-fields"} items={otherFieldsPromise}/>
                    </div>

                </div>
                <button className="navbutton" onClick={e => {
                    e.preventDefault();
                    navigate("/job");}}>
                    Tovább</button>
            </div>
        </div>
        );
}