import React from 'react'
import ReactDOM from 'react-dom/client'
import {ChakraProvider} from '@chakra-ui/react'
import Customers from './Customers'
import { createStandaloneToast } from '@chakra-ui/toast'
import { createBrowserRouter, RouterProvider} from 'react-router-dom'
import Login from "./components/login/Login"
import AuthProvider from './components/context/AuthContext'
import ProtectedRoute from "./components/shared/ProtectedRoute"
import Signup from "./components/signup/Signup"
import Home from "./Home"
import Settings from "./Settings"
import './index.css'

const { ToastContainer } = createStandaloneToast()
const router=createBrowserRouter(
    [
        {path:"/",element:<Login/>},
        {path:"/signup",element:<Signup/>},
        {path:"/dashboard", element: <ProtectedRoute><Home/></ProtectedRoute>},
        {path:"/dashboard/customers", element: <ProtectedRoute><Customers/></ProtectedRoute>},
        {path:"/dashboard/settings", element: <ProtectedRoute><Settings/></ProtectedRoute>},
    ]
)

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
      <ChakraProvider>
          <AuthProvider>
             <RouterProvider router={router}/>
          </AuthProvider>
          <ToastContainer/>
      </ChakraProvider>

  </React.StrictMode>,
)
