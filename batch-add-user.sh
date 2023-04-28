#!/bin/bash

domain=https://ichat.x-assn.xyz

while read line
do
  curl --location --request POST $domain'/api/auth/register' \
--header 'Content-Type: application/json' \
--data-raw '{ "username": "'$line'", "password": "'$line'","register_code":"upgrop@1949" }'

curl --location --request POST $domain'/api/user/upgrade' \
--header 'Content-Type: application/json' \
--data-raw '{ "username": "'$line'","register_code":"upgrop@1949" }'
done < add_users.txt

