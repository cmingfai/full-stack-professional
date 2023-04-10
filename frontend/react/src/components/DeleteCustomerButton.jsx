import React from 'react'

import {
    Button,
    AlertDialog,
    AlertDialogBody,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogContent,
    AlertDialogOverlay,
    useDisclosure,
} from '@chakra-ui/react'

import {deleteCustomer} from "../services/client"
import {successNotification, errorNotification} from "../services/notification"

function DeleteCustomerButton({customerId, fetchCustomers}) {
    const { isOpen, onOpen, onClose } = useDisclosure()
    const cancelRef = React.useRef()

    const deleteCustomerById = (id) => {
        deleteCustomer(id)
            .then(res=>{
                 console.log(res);
                 successNotification("Customer deleted",
                     `Customer with ID ${id} was successfully deleted.`)
                 fetchCustomers()
            })
            .catch(err=> {
                errorNotification(err.code,err.response.data.message);
            })
            .finally(()=>{
                onClose()
            })
    }

    return (
        <>
            <Button colorScheme='red' onClick={onOpen} rounded={"full"} _hover={{
                bg: "red.500",
                color: "white",
                transform: "translateY(-2px)",
                boxShadow: "lg"
            }} _focus={
                {bg:"red.500"}
            }>
                Delete
            </Button>

            <AlertDialog
                isOpen={isOpen}
                leastDestructiveRef={cancelRef}
                onClose={onClose}
            >
                <AlertDialogOverlay>
                    <AlertDialogContent>
                        <AlertDialogHeader fontSize='lg' fontWeight='bold'>
                            Delete Customer
                        </AlertDialogHeader>

                        <AlertDialogBody>
                            Are you sure to delete customer with ID {customerId}? You can't undo this action afterwards.
                        </AlertDialogBody>

                        <AlertDialogFooter>
                            <Button ref={cancelRef} onClick={onClose}>
                                Cancel
                            </Button>
                            <Button colorScheme='red' onClick={()=> {deleteCustomerById(customerId)}} ml={3}>
                                Delete
                            </Button>
                        </AlertDialogFooter>
                    </AlertDialogContent>
                </AlertDialogOverlay>
            </AlertDialog>
        </>
    )
}

export {DeleteCustomerButton};