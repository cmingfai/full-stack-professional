import {useEffect} from "react"
import {useNavigate} from "react-router-dom"
import {useAuth} from "../context/AuthContext"

const ProtectedRoute=({children}) => {
    const {isCustomerAuthenticated} = useAuth();
    const navigate=useNavigate()

    useEffect(()=>{
        console.log(`isCustomerAuthenticated:${isCustomerAuthenticated()}`)
        if (!isCustomerAuthenticated()) {
            navigate("/")
        }
    })

    return isCustomerAuthenticated()?children:""
}

export default ProtectedRoute;