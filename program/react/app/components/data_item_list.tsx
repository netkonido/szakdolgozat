import type { Route } from "../../.react-router/types/app/+types/root";
import {Await, data, Form, useFetcher, useLoaderData, useRevalidator} from "react-router";
import React, { Suspense, useState} from "react";
import axios from "axios";

interface props{
    name:string,
    apiEndpoint:string,
    items : Promise<Item[]>,
}

type Item = { id: number; content: string };

function DataItem({item, apiEndpoint}: {item: Item, apiEndpoint : string})
{
    const revalidator = useRevalidator();
    const [editing, setEditing] = useState(false);
    return <div className="flex-col p-2">
        <div className="flex flex-nowrap w-full max-w-full" hidden={editing}>
            <button
                className=" w-fit text-xs border rounded-l-md border-r-0 border-black p-1 bg-lime-400 hover:bg-lime-500 text-black"
                onClick={e => {
                    e.preventDefault();
                    setEditing(true);
                    revalidator.revalidate();
                }}>Szerkesztés
            </button>
            <button className=" w-fit text-xs border rounded-r-md mr-5 border-black p-1 bg-amber-500 hover:bg-amber-600 text-black"
                    onClick={event => {
                        event.preventDefault();
                        axios.delete(`http://localhost:8080/api/v1/data${apiEndpoint}`, {withCredentials: true, headers:{"Content-Type":"multipart/form-data"}, data:{"id": item.id}})
                            .then(res=> revalidator.revalidate())
                            .catch(err => console.log("Could not delete element" + apiEndpoint + " id: " + item.id));
                        setEditing(false)

                    }}>Törlés
            </button>
            <p className="text-wrap truncate max-w-10/12">{item.content}</p>
        </div>
        <div hidden={!editing}>
            <Form id="editForm" className="w-full" onSubmit={e=> {

                e.preventDefault();
                const inputField = e.currentTarget.elements.namedItem("content") as HTMLFormElement;
                const formData = {
                    content: inputField.value,
                    id: item.id,
                }
                axios.patch(`http://localhost:8080/api/v1/data${apiEndpoint}`, formData, {withCredentials: true, headers:{"Content-Type":"multipart/form-data"}})
                    .catch(err => console.log("Could not edit element" + apiEndpoint + " id: " + item.id));
                setEditing(false);
                inputField.content = "";
                revalidator.revalidate();
            }}>
                <input required={true} type="text" id="content" maxLength={200} defaultValue={item.content} className=" w-3/4 p-3 h-10 border-2 border-black rounded-l-md bg-white  hover:bg-gray-200"/>
                <button type="submit" className="h-10 w-1/8 text-md border-black border-2 border-l-0 border-r-0 bg-lime-400 hover:bg-lime-500">OK</button>
                <button type="reset" className="h-10 w-1/8 text-md  border-black border-2 rounded-r-md bg-amber-500 hover:bg-amber-600" onClick={
                    event => {
                        setEditing(false);
                    }
                }>Mégse</button>
            </Form>
        </div>
        <hr className={"m-2"}/>
        </div>
}

export default function DataItemList({name, apiEndpoint, items}: props) {

    const [newElementHidden, setNewElementHidden] = useState(true);
    const revalidator = useRevalidator();
    const itemList = [{id:1, content:"abc"},{id:2, content:"def"}]
    return (
        <div className="flex-col p-3">
            <div className="flex items-end justify-between p-3">
                <h2 className="h-fit text-3xl font-semibold text-black">{name}</h2>
                <button className="h-10 w-10 text-center font-bold text-xl rounded-xl border-2 border-lime-900 bg-lime-400 hover:bg-lime-500" onClick={e =>{
                    e.preventDefault();
                    setNewElementHidden(!newElementHidden);
                    revalidator.revalidate();
                }}>+</button>
            </div>
            <hr/>
            <div className="flex-col">
                <Suspense fallback={<div className={"animate-pulse"}>Betöltés...</div>}>
                    <Await resolve={items}>
                        {(value) => value
                            .sort((a, b)=> {return a.id - b.id})
                            .map((i: Item) => {
                                return(<DataItem item={i} apiEndpoint={apiEndpoint} key={i.id}/>);
                            })}
                    </Await>
                </Suspense>
                <label className="p-3 text-base" hidden={newElementHidden}>Új elem:</label>
                <div hidden={newElementHidden} className="flex flex-nowrap p-3 w-full">
                    <Form className="w-full" onSubmit={e=> {

                        e.preventDefault();
                        const inputField = e.currentTarget.elements.namedItem("content") as HTMLFormElement;
                        const formData = {
                            content: inputField.value,
                        }
                        axios.post(`http://localhost:8080/api/v1/data${apiEndpoint}`, formData, {withCredentials: true, headers:{"Content-Type":"multipart/form-data"}})
                            .then(res=> revalidator.revalidate())
                            .catch(err => console.log(err.status));
                        setNewElementHidden(true);
                        inputField.value = "";
                    }}>
                        <input required={true} type="text" maxLength={200} id="content" placeholder="Tartalom" className=" h-10 w-3/4 p-3 border-2 border-black rounded-l-md bg-white  hover:bg-gray-200"/>
                        <button type="submit" className="h-10 w-1/8 text-md border-black border-2 border-l-0 border-r-0 bg-lime-400 hover:bg-lime-500">OK</button>
                        <button type="reset" className="h-10 w-1/8 text-md  border-black border-2 rounded-r-md bg-amber-500 hover:bg-amber-600" onClick={
                            event => {
                                setNewElementHidden(true);
                                revalidator.revalidate();
                            }
                        }>Mégse</button>
                    </Form>
                </div>
            </div>
        </div>
    )
}