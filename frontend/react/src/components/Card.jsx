import {
    Heading,
    Avatar,
    Box,
    Center,
    Image,
    Flex,
    Text,
    Stack,
    Tag,
    useColorModeValue,
    Button,
} from '@chakra-ui/react';

import {successNotification, errorNotification} from "../services/notification"
import {DeleteCustomerButton} from "./DeleteCustomerButton"
import EditCustomerDrawer from "./EditCustomerDrawer"

const getRandomInt = max=> {
    return Math.floor(Math.random() * max);
}

export default function CardWithImage({id, name, email, age, gender, imageNumber, fetchCustomers}) {
    const sex=gender==="MALE"?"men":"women";
    // const randomInt=getRandomInt(99);
    const randomInt=imageNumber;

    return (
        <Center py={6}>
            <Box
                maxW={'250px'}
                minW={'250px'}
                w={'full'}
                bg={useColorModeValue('white', 'gray.800')}
                boxShadow={'2xl'}
                rounded={'md'}
                overflow={'hidden'}>
                <Image
                    h={'120px'}
                    w={'full'}
                    src={
                        'https://images.unsplash.com/photo-1612865547334-09cb8cb455da?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80'
                    }
                    objectFit={'cover'}
                />
                <Flex justify={'center'} mt={-12}>
                    <Avatar
                        size={'xl'}
                        src={`https://randomuser.me/api/portraits/${sex}/${randomInt}.jpg`}
                        alt={'Author'}
                        css={{
                            border: '2px solid white',
                        }}
                    />
                </Flex>

                <Box p={1}  mb={0}>
                    <Stack spacing={2} align={'center'} mb={5}>
                        <Tag borderRadius={"full"}>{id}</Tag>
                        <Heading fontSize={'2xl'} fontWeight={500} fontFamily={'body'}>
                            {name}
                        </Heading>
                        <Text color={'gray.500'}>{email}</Text>
                        <Text color={'gray.500'}>Age {age} | {gender}</Text>
                       </Stack>
                </Box>

                <Stack p={2} direction={"row"} spacing={2}>
                    <Stack w={"full"} mt={0}>

                    <EditCustomerDrawer id={id} initialValues={{name, email, age, gender}} fetchCustomers={fetchCustomers}></EditCustomerDrawer>
                    </Stack>
                    <Stack w={"full"}>

                    <DeleteCustomerButton fetchCustomers={fetchCustomers} customerId={id}></DeleteCustomerButton>
                    </Stack>
                </Stack>

            </Box>



        </Center>
    );
}