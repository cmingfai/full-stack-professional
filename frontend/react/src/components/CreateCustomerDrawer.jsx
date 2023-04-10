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

import CreateCustomerForm from "./shared/CreateCustomerForm";

const AddIcon = () => "+";
const CloseIcon = () => "x";

const CreateCustomerDrawer=({fetchCustomers})=>{
    const { isOpen, onOpen, onClose } = useDisclosure()

    return (
        <>
        <Button
            leftIcon={<AddIcon/>}
            colorScheme={'red'}
            onClick={onOpen}>Create customer</Button>
    <Drawer isOpen={isOpen} onClose={onClose} size={'md'}>
        <DrawerOverlay />
        <DrawerContent>
            <DrawerCloseButton />
            <DrawerHeader>Create new customer</DrawerHeader>

            <DrawerBody>
               <CreateCustomerForm fetchCustomers={fetchCustomers}/>
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

export default CreateCustomerDrawer;


