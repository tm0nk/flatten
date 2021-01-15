# flatten

## Before you begin

* `git` must be installed locally
* Either `gradle` or `docker` must be installed locally

## Clone the repo

* Clone this repo locally and cd into the `flatten` dir:

```
$ git clone https://github.com/tm0nk/flatten.git
$ cd flatten
```

## Build the code

**Note:** If you already have `gradle` installed locally, skip to step 4

1. Run `docker pull gradle:6.8.0`
    * Make sure you are pulling from public hub.docker.com
2. Run `docker run -it -v${PWD}:/home/gradle/flatten gradle:6.8.0 bash`
    * This launches a controlled environment to make the `gradle` command available to you even if it's not installed locally
    * Subsequent commands will be executed inside the container environment
3. Run `cd flatten`
4. Run `gradle check`
    * The build is done when you see:

```
BUILD SUCCESSFUL in 637ms
4 actionable tasks: 4 up-to-date
```

## Sample usage

Invoke the executable with `./flatten`

* Pass a file in on stdin with shell redirection: `./flatten < src/test/resources/fixture1.json `
* Pass any string in on stdin with pipes: `echo '{"a": 1}' | ./flatten `

For example:

```
root@d3a876f4b050:/home/gradle/flatten# ./flatten < src/test/resources/fixture1.json
{
  "a": 1,
  "b": true,
  "c.d": 3,
  "c.e": "test"
}
root@d3a876f4b050:/home/gradle/flatten# echo '{"a": 1}' | ./flatten
{
  "a": 1
}
root@d3a876f4b050:/home/gradle/flatten#
```
