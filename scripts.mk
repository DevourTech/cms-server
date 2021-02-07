define format_if_executable_present =
	prettier --write "**/*.java"
	if [ $? -ne 0 ]; then
		echo "prettier is not installed on your system. Run make install-formatter to install prettier."
	fi
endef
