import {Button,
    useDisclosure,
    Drawer,
    DrawerOverlay,
    DrawerContent,
    DrawerCloseButton,
    DrawerBody,
    DrawerHeader,
    DrawerFooter,
    Input
} from '@chakra-ui/react';

import EditCustomerForm from "./shared/EditCustomerForm";


const CloseIcon = () => "x";

const EditCustomerDrawer=({id, initialValues, fetchCustomers})=>{
    const { isOpen, onOpen, onClose } = useDisclosure()

    return (
        <>
        <Button
             colorScheme={'red'}
            onClick={onOpen}
             rounded={"full"} _hover={{
            bg: "red.500",
            color: "white",
            transform: "translateY(-2px)",
            boxShadow: "lg"
        }} _focus={
            {bg:"red.500"}
        }>Edit</Button>
    <Drawer isOpen={isOpen} onClose={onClose} size={'md'}>
        <DrawerOverlay />
        <DrawerContent>
            <DrawerCloseButton />
            <DrawerHeader>Edit customer</DrawerHeader>

            <DrawerBody>
               <EditCustomerForm id={id} initialValues={initialValues} fetchCustomers={fetchCustomers}/>
            </DrawerBody>

            <DrawerFooter>
                <Button
                    leftIcon={<CloseIcon/>}
                    colorScheme={'red'}
                    onClick={onClose}>Close</Button>
            </DrawerFooter>
        </DrawerContent>
    </Drawer>
        </>
    )
}

export default EditCustomerDrawer;


