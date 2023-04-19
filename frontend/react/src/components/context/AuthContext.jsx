import {
    useContext,
    createContext,
    useEffect,
    useState
} from "react"

import {login as performLogin} from "../../services/client"
import jwtDecode from "jwt-decode"

const AuthContext=createContext({})

const AuthProvider=({children}) => {
    const [customer,setCustomer]=useState(null);

    const setCustomerFromToken=()=>{
        let token=localStorage.getItem("access_token")
        if (token) {
            const decodedToken=jwtDecode(token)
            setCustomer({
                username: decodedToken.sub,
                roles: decodedToken.roles
            })
        }
    }

    useEffect(()=>{
        setCustomerFromToken();
    },[])

    const login=async(usernameAndPassword) => {
       return new Promise((resolve,reject)=>{
           performLogin(usernameAndPassword)
               .then(res=>{
                  const jwtToken=res.headers["authorization"]
                   //save token
                   localStorage.setItem("access_token",jwtToken)
                   const decodedToken=jwtDecode(jwtToken)
                   setCustomer({
                       username: decodedToken.sub,
                       roles: decodedToken.roles
                   })
                   // console.log("<<<customer>>>",customer)
                   resolve(res);
               }).catch(err=>{
                   reject(err)
               })
       })
    }

    const logout= ()=> {
        localStorage.removeItem("access_token")
        setCustomer(null);
    }

    const isCustomerAuthenticated=()=> {
        const token=localStorage.getItem("access_token")
        if (!token) {
            return false
        }

        const decodedToken=jwtDecode(token)
        const {exp: expiration}=decodedToken
        if (Date.now()>expiration*1000) {
            logout()
            return false
        }

        return true
    }

    return (
        <AuthContext.Provider value={{customer,login,logout,isCustomerAuthenticated,setCustomerFromToken}}>
            {children}
        </AuthContext.Provider>
    )
}

export const useAuth=()=> useContext(AuthContext);
export default AuthProvider;