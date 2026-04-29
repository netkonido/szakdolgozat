import React from "react";
import type { Route } from "./+types/home";
import {data, Form, Link, redirect, useFormAction, useNavigate} from "react-router";
import type {ActionFunctionArgs} from "react-router";
import axios from "axios";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Önéletrajz generátor" },
    { name: "description", content: "Ez az önéletrajz generátor nyitólapja" },
  ];
}

export async function clientLoader()
{
}

clientLoader.hydrate = true as const;

export default function Home(props: Route.ComponentProps) {
    const navigate = useNavigate();
    const action = useFormAction();
    return (
          <div className="flex flex-col items-center bg-lime-300">
              <h1 className="text-5xl font-semibold p-10 text-black">Kiberbiztonsági Önéletrajz Generátor</h1>
              <div className="flex items-stretch w-full h-full">
                  {[1,2,3,4].map((num) =>(<div key={num} className="flex flex-1 h-150 bg-lime-400 p-5 m-10 content-center text-center items-center"> <h1 className="text-5xl text-center w-full font-bold">{num}</h1></div>))}
              </div>
              <button type="button" className="navbutton" onClick={(e) => {
                    e.preventDefault();
                    axios.get("http://localhost:8080/api/v1/session/get",{withCredentials:true})
                        .then((response) => {
                        navigate("/data");
                        })
                        .catch(err => {
                            axios.get("http://localhost:8080/api/v1/session/new",{withCredentials:true})
                                .then((response) => {
                                    navigate("/data");
                                }).catch(err =>
                                console.log("Could not create session" + err.toString()));
                        });
              }}>Kezdés</button>
          </div>
          );
}
