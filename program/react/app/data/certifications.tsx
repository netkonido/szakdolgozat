import {Route} from "../../.react-router/types/app/+types/root";
import ClientLoaderArgs = Route.ClientLoaderArgs;

export async function getData({params,} : Route:ClientLoaderArgs) {
    const response = fetch(`http://localhost:8080/data`, {})

}

export default function Certifications() {
    return (
        <div className="flex-col p-5">
            <h2 className="text-xl text-black p-5">Képzettségek</h2>
            <hr/>
            <div>
                <p>ide jön a minden</p>
            </div>
        </div>
    )
}