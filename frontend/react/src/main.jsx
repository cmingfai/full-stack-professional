import React from 'react'
import ReactDOM from 'react-dom/client'
import {ChakraProvider} from '@chakra-ui/react'
import App from './App'
import { createStandaloneToast } from '@chakra-ui/toast'
import { createBrowserRouter, RouterProvider} from 'react-router-dom'
import Login from "./components/login/Login"
import AuthProvider from './components/context/AuthContext'
import ProtectedRoute from "./components/shared/ProtectedRoute"

import './index.css'

const { ToastContainer } = createStandaloneToast()
const router=createBrowserRouter(
    [
        {path:"/",element:<Login/>},
        {path:"dashboard", element: <ProtectedRoute><App/></ProtectedRoute>}
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
