import { Formik, Form, useField } from 'formik';
import {Button, FormLabel, Input, Select, Alert, AlertIcon, Box, Stack, Text} from '@chakra-ui/react';
import * as Yup from 'yup';
import {saveCustomer} from "../../services/client"
import {successNotification, errorNotification} from "../../services/notification";

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

// And now we can use these
const CreateCustomerForm = ({fetchCustomers}) => {
    return (
        <>
             <Formik
                initialValues={{
                    name: '',
                    email: '',
                    age: 0,
                    gender: '',
                }}
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
                    saveCustomer(customer).then(
                        res=> {
                            fetchCustomers();
                            console.log(res)
                            // alert("customer saved")
                            successNotification("Customer saved",`${customer.name} was successfully saved`)
                        }
                    ).catch(err=>{
                        console.log(err)
                        errorNotification(err.code,err?.response.data.message);
                    }).finally(()=>{
                            setSubmitting(false);
                    })


                }}
            >
                 {({isValid, isSubmitting})=> {
                     console.log("isValid: "+isValid+", isSubmitting: "+isSubmitting)
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


                             <Button isDisabled={!isValid || isSubmitting} type="submit">Submit</Button>


                         </Stack>
                     </Form>
                 )}
                 }
            </Formik>
        </>
    );
};

export default CreateCustomerForm;