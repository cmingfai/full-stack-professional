import React from 'react'
import ReactDOM from 'react-dom/client'
import {ChakraProvider} from '@chakra-ui/react'
import App from './App'

import { createStandaloneToast } from '@chakra-ui/toast'

import './index.css'

const { ToastContainer } = createStandaloneToast()


ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
      <ChakraProvider>
         <App />
          <ToastContainer/>
      </ChakraProvider>

  </React.StrictMode>,
)
