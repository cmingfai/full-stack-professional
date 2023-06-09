import {Wrap,WrapItem, Spinner, Text} from "@chakra-ui/react"
import SidebarWithHeader from "./components/shared/SideBar"
import CardWithImage from "./components/Card";
import CreateCustomerDrawer from "./components/CreateCustomerDrawer";
import {useEffect, useState} from "react";
import {getCustomers} from "./services/client.js"
import {errorNotification} from "./services/notification";

const Customers=()=>{
    const [customers, setCustomers]=useState([])
    const [loading, setLoading]=useState(false)
    const [error, setError]=useState("")

    const fetchCustomers=() => {
        setLoading(true)
        getCustomers()
            .then(res=>{
                setCustomers(res.data)
            })
            .catch(err=> {
                console.log(err)
                setError(err?.response.data.message)
                errorNotification(err.code,err?.response.data.message);
            })
            .finally(()=>setLoading(false))
    }
    useEffect(()=>{
       fetchCustomers();
    },[])

    if (loading) {
        return (
            <SidebarWithHeader>
                <Spinner
                    thickness='4px'
                    speed='0.65s'
                    emptyColor='gray.200'
                    color='blue.500'
                    size='xl'
                />
            </SidebarWithHeader>
        )
    }

    if (error) {
        return (
            <SidebarWithHeader>
                <CreateCustomerDrawer fetchCustomers={fetchCustomers}/>
                <Text mt={2}>Oops, there is an error.</Text>
            </SidebarWithHeader>
        )
    }

    if (customers.length<=0) {
        return (
            <SidebarWithHeader>
                <CreateCustomerDrawer fetchCustomers={fetchCustomers}/>
                <Text mt={2}>No customers available</Text>
            </SidebarWithHeader>
        )
    }


    return (
         <SidebarWithHeader>
             <CreateCustomerDrawer fetchCustomers={fetchCustomers}/>

             <Wrap justify={"center"} spacing={"30px"}>

            {customers.map((customer,index)=>{
                return (
                <WrapItem key={index}>
                    <CardWithImage {...customer} imageNumber={customer.id%100} fetchCustomers={fetchCustomers}></CardWithImage>
                </WrapItem>
                )
            })}
            </Wrap>
        </SidebarWithHeader>
    )
}

export default Customers
