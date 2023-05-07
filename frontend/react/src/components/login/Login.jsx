import {
    Box,
    Button,
    Flex,
    FormLabel,
    Heading,
    Input,
    Link,
    Stack,
    Image,
    Text,
    Alert,
    AlertIcon
} from '@chakra-ui/react';

import {Formik, Form, useField, setNestedObjectValues} from "formik";
import * as Yup from "yup";
import { useEffect, useRef} from "react";
import {useAuth} from "../context/AuthContext"
import {errorNotification} from "../../services/notification"
import {useNavigate} from 'react-router-dom'
import RegisterCustomerDrawer from "../RegisterCustomerDrawer"

const MyTextInput = ({ label, ...props }) => {
    // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
    // which we can spread on <input>. We can use field meta to show an error
    // message if the field is invalid and it has been touched (i.e. visited)
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />

            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={2}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}


        </Box>
    );
};

const LoginForm = () => {
    const {login}=useAuth();
    const navigate=useNavigate();

    return (
    <Formik
              validateOnMount={true}
              validationSchema={Yup.object(
                {
                    username: Yup.string()
                        .email("Must be a valid email")
                        .required("Email is required"),
                    password: Yup.string()
                        .max(20,"Password cannot be more than 20 characters")
                        .required("Password is required")
                }
              )
            }
            initialValues={{username:'',password:''}}

            onSubmit={(values,{setSubmitting})=>{
                setSubmitting(true)
                // alert(JSON.stringify(values,null,0));
                login(values).then(res=> {
                    // navigate to dashboard
                    navigate("/dashboard/customers")
                   console.log("successfully logged in")
                }).catch(err=>{
                     errorNotification(err.code,err.response.data.message);
                     console.log(err)
                }).finally(()=>{
                    setSubmitting(false)
                })
            }}>

        {
            ({isValid,isSubmitting, dirty})=>{
                // console.log("isValid="+isValid+", isSubmitting="+isSubmitting)
                return (
            <Form>
                <Stack spacing={15}>
                        <MyTextInput
                            label={"Email"}
                            name={"username"}
                            type={"text"}
                            placeholder={"hello@amigoscode.com"}
                        />
                    <MyTextInput
                        label={"Password"}
                        name={"password"}
                        type={"password"}
                        placeholder={"Type your password"}
                    />
                    <Button isDisabled={!isValid|| isSubmitting}  type="submit">
                         Login
                    </Button>

                </Stack>

            </Form>
           )}
        }

    </Formik>
   );
};

const Login= ()=> {
    const {customer, isCustomerAuthenticated}=useAuth()
    const navigate=useNavigate()

    useEffect(()=>{

        if (isCustomerAuthenticated()) {
            navigate("/dashboard/customers")
        }
    },[])



    return (
        <Stack minH={'100vh'} direction={{ base: 'column', md: 'row' }}>
            <Flex p={8} flex={1} alignItems={'center'} justifyContent={'center'}>
                <Stack spacing={4} w={'full'} maxW={'md'}>
                    <Stack alignItems={'center'} >
                    <Image
                        src={'https://user-images.githubusercontent.com/40702606/210880158-e7d698c2-b19a-4057-b415-09f48a746753.png'}
                    boxSize={"200px"}
                    alt={"Amigoscode Logo"}/>
                    </Stack>
                    <Heading fontSize={'2xl'} mb={15}>Sign in to your account</Heading>
                     <LoginForm/>

                    <Link color={"blue.500"} href={"/signup"}>Don't have an account? Sign up now.</Link>
                </Stack>
            </Flex>
            <Flex flex={1} p={10} flexDirection={'column'}
            alignItems={'center'} justifyContent={'center'}
            bgGradient={{sm:'linear(to-r,blue.600,purple.600)'}}>
                <Text fontSize={'6xl'} color={'white'} fontWeight={'bold'} mb={5}>
                    <Link href={"https://amigoscode.com/courses"}>Enrol Now</Link>
                </Text>
                <Image
                    alt={'Login Image'}
                    objectFit={'scale-down'}
                    src={
                        'https://user-images.githubusercontent.com/40702606/215539167-d7006790-b880-4929-83fb-c43fa74f429e.png'
                    }
                />
            </Flex>
        </Stack>
    );
}

export default Login