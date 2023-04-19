import {Button,
    useDisclosure,
    Drawer,
    DrawerOverlay,
    DrawerContent,
    DrawerCloseButton,
    DrawerBody,
    DrawerHeader,
    DrawerFooter,
    Input,
    Link
} from '@chakra-ui/react';

import RegisterCustomerForm from "./shared/RegisterCustomerForm";

// const AddIcon = () => "+";
const CloseIcon = () => "x";

const RegisterCustomerDrawer=({fetchCustomers})=>{
    const { isOpen, onOpen, onClose } = useDisclosure()

    return (
        <>
        <Button
             colorScheme={'blue'}
            onClick={onOpen}>Not registered?</Button>
    <Drawer isOpen={isOpen} onClose={onClose} size={'md'}>
        <DrawerOverlay />
        <DrawerContent>
            <DrawerCloseButton />
            <DrawerHeader>Register new user</DrawerHeader>

            <DrawerBody>
               <RegisterCustomerForm fetchCustomers={fetchCustomers}/>
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

export default RegisterCustomerDrawer;


