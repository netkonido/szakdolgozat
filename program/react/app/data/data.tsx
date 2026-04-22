import {Link, useNavigate} from "react-router";
import {TopBar} from "~/components/topBar";
import type {Route} from "../../.react-router/types/app/+types/root";
import Educations from "~/data/educations";
import Certifications from "~/data/certifications";
import WorkExperiences from "~/data/work_experiences";
import OtherFields from "~/data/other_fields";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Data Entry" },
    { name: "description", content: "This page can be used to enter personal data." },
  ];
}

export default function Data() {
    const navigate = useNavigate();
    return (
        <div>
            <TopBar
                title={"Adatok Bevitele"}
            />
            <div className="flex flex-col items-center bg-gray-400 p-5">
                <button type="button" className="navbutton" onClick={e => {e.preventDefault()
                navigate("/data/import")}}>
                    Adatok importálása</button>

                <div className="flex w-full">
                    <div className="flex-1/3 items-center p-5 m-5 border-2 border-gray-200">
                        <h2 className="text-black text-3xl font-semibold p-3">Alapadatok bevitele</h2>
                        <div className="m-3">
                            <h3 className="text-xl text-black p-2">Név</h3>
                            <input type="text" className="bg-white hover:bg-gray-200 h-10 w-full border-2 border-black rounded-md p-2"/>
                        </div>
                        <div className="m-3">
                            <h3 className="text-xl text-black p-2">Email cím</h3>
                            <input type="email" className="bg-white hover:bg-gray-200 h-10 w-full border-2 border-black rounded-md p-2"/>
                        </div>
                        <div className="m-3">
                            <h3 className="text-xl text-black p-2">Telefonszám</h3>
                            <input type="tel" className="bg-white hover:bg-gray-200 h-10 w-full border-2 border-black rounded-md p-2"/>
                        </div>
                    </div>
                    <div className="flex-col flex-2/3 items-center">
                        <Educations/>
                        <Certifications/>
                        <WorkExperiences/>
                        <OtherFields/>
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