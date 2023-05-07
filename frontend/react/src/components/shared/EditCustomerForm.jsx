import { Formik, Form, useField } from 'formik';
import {Button, FormLabel, Input, Select, Alert, AlertIcon, Box, Stack, VStack, Image} from '@chakra-ui/react';
import * as Yup from 'yup';
import {customerProfilePictureUrl, updateCustomer, uploadCustomerProfilePicture} from "../../services/client"
import {successNotification, errorNotification} from "../../services/notification";
import {useCallback, useState} from "react";
import {useDropzone} from "react-dropzone";


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


const MySelect = ({ label, ...props }) => {
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Select {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={2}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const MyDropzone = ({customerId, fetchCustomers}) => {
    const onDrop = useCallback(acceptedFiles => {
        const formData=new FormData();
        formData.append("file",acceptedFiles[0]);
        uploadCustomerProfilePicture(customerId,formData)
            .then(()=> {
                successNotification("Success","Customer profile image uploaded.");
                fetchCustomers();
            })
            .catch(()=>errorNotification("Error","Failed to upload customer profile image."));
    }, [])
    const {getRootProps, getInputProps, isDragActive} = useDropzone({onDrop})

    return (
        <Box {...getRootProps()}
            w={'100%'}
             textAlign={'center'}
        border={'dashed'}
        borderRadius={'3xl'}
        borderColr={'gray.200'}
        p={6}
             rounded={'md'}
        >
            <input {...getInputProps()} />
            {
                isDragActive ?
                    <p>Drop the picture here ...</p> :
                    <p>Drag 'n' drop picture here, or click to select picture</p>
            }
        </Box>
    )
}

// And now we can use these
const EditCustomerForm = ({id, initialValues, fetchCustomers}) => {

    return (
        <>
            <VStack spacing={5} mb={5}>
                <Image
                    borderRadius={'full'}
                    boxSize={'150px'}
                    objectFit={'cover'}
                    src={customerProfilePictureUrl(id)}
                    border={'solid'}
                />
              <MyDropzone customerId={id}
              fetchCustomers={fetchCustomers}/>
             </VStack>
             <Formik
                initialValues={initialValues}
                validationSchema={Yup.object({
                    name: Yup.string()
                        .max(20, 'Must be 20 characters or less')
                        .required('Required'),
                    email: Yup.string()
                        .email('Invalid email address')
                        .required('Required'),
                    age: Yup.number()
                        .min(16,'Must be 16 or more years of age')
                        .max(100,'Must be less than 100 years of age')
                        .required('Required'),
                     gender: Yup.string()
                        .oneOf(
                            ['MALE', 'FEMALE'],
                            'Invalid gender'
                        )
                        .required('Required'),
                })}
                onSubmit={(customer, { setSubmitting }) => {
                    setSubmitting(true);
                    updateCustomer(id, customer).then(
                        res=> {
                            fetchCustomers();
                            console.log(res)
                            // alert("customer saved")
                            successNotification("Customer updated",`Customer with ID ${id} was successfully updated`)
                        }
                    ).catch(err=>{
                        console.log(err)
                        errorNotification(err.code,err?.response.data.message);
                    }).finally(()=>{
                            setSubmitting(false);
                    })


                }}
            >
                 {({isValid, isSubmitting, dirty})=> {
                     console.log("isValid: "+isValid+", isSubmitting: "+isSubmitting+", dirty:"+dirty)
                     return (
                     <Form>
                         <Stack spacing={"24px"}>
                             <MyTextInput
                                 label="Name"
                                 name="name"
                                 type="text"
                                 placeholder="Jane"
                             />

                             <MyTextInput
                                 label="Email Address"
                                 name="email"
                                 type="email"
                                 placeholder="jane@formik.com"
                             />

                             <MyTextInput
                                 label="Age"
                                 name="age"
                                 type="number"
                                 placeholder={20}
                             />

                             <MySelect label="Gender" name="gender">
                                 <option value="">Select gender</option>
                                 <option value="MALE">Male</option>
                                 <option value="FEMALE">Female</option>

                             </MySelect>


                             <Button isDisabled={!isValid ||!dirty || isSubmitting } type="submit">Update</Button>


                         </Stack>
                     </Form>
                 )}
                 }
            </Formik>
        </>
    );
};

export default EditCustomerForm;