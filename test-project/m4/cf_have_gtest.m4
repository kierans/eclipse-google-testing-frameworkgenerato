# ===========================================================================
#        TODO: Add URL
# ===========================================================================
#
# SYNOPSIS
#
#   CF_HAVE_GTEST([VERSION]) [--with-gtest=<dir>]
#
# DESCRIPTION
#
#   Specifies whether to use the Google C++ Testing framework (gtest),
#   and where gtest lives.
#
#   The following variables are exported:
#
#   GTEST_CPPFLAGS
#   GTEST_CXXFLAGS
#   GTEST_LDFLAGS
#   GTEST_LIBS
#
#   Example lines for Makefile.in:
#
#     GTEST_CXXFLAGS = @GTEST_CXXFLAGS@
# 
#   Options:
#
#   --with-gtest=<dir>
#
#   Specifies the directory where gtest lives
#
# LICENSE
#
#   TODO: License

AC_DEFUN([CF_HAVE_GTEST], [
  dnl wrapper around GTEST_LIB to clean up the output.
  AC_REQUIRE([CF_PROG_CUT])

  AC_ARG_WITH([gtest],
    [AS_HELP_STRING([--with-gtest],
                   [Location of the Google C++ Testing Framework.])
  ])
  
  AC_ARG_VAR([GTEST_CONFIG], [The exact path of Google Test's 'gtest-config' script.])
  AC_ARG_VAR([GTEST_CPPFLAGS], [C-like preprocessor flags for Google Test.])
  AC_ARG_VAR([GTEST_CXXFLAGS], [C++ compile flags for Google Test.])
  AC_ARG_VAR([GTEST_LDFLAGS], [Linker path and option flags for Google Test.])
  AC_ARG_VAR([GTEST_LIBS], [Library linking flags for Google Test.])
  AC_ARG_VAR([GTEST_VERSION], [The version of Google Test available.])

  AC_MSG_CHECKING([for gtest])
  AS_IF([test x"$with_gtest" = x""],
    [AC_MSG_ERROR([Don't know where to look for gtest (see configure --help)])],
    [AC_MSG_RESULT([ok])
  ])
    
  AC_MSG_CHECKING([for 'gtest-config'])
  AS_IF([test -x "$with_gtest/scripts/gtest-config"],
    [GTEST_CONFIG="$with_gtest/scripts/gtest-config"],
    [GTEST_CONFIG="$with_gtest/bin/gtest-config"]
  )
  
  AS_IF([test -x "${GTEST_CONFIG}"],
    [AC_MSG_RESULT([${GTEST_CONFIG}])],
    [
      AC_MSG_RESULT([no])
      AC_MSG_ERROR([dnl
Unable to locate either a built or installed Google Test.
The specific location '$with_gtest' was provided for a built or installed
Google Test, but no 'gtest-config' script could be found at this location.
    ])
  ])
  
  m4_ifval([$1],
    [gtest_min_version="$1"],
    [gtest_min_version="0"]
  )

  AC_MSG_CHECKING([for Google Test at least version >= $gtest_min_version])
     
  AS_IF([${GTEST_CONFIG} --min-version=${gtest_min_version}],
    [AC_MSG_RESULT([yes])],
    [AC_MSG_RESULT([no])
     AC_MSG_ERROR([dnl
Google Test was enabled, but no viable version could be found.])
    ]
  )
  
  GTEST_CPPFLAGS=`${GTEST_CONFIG} --cppflags`
  GTEST_CXXFLAGS=`${GTEST_CONFIG} --cxxflags`
  GTEST_LDFLAGS=`${GTEST_CONFIG} --ldflags`
  GTEST_LIBS=`${GTEST_CONFIG} --libs`
  GTEST_VERSION=`${GTEST_CONFIG} --version`

  _CF_FILTER_DUPLICATES([GTEST_CPPFLAGS], [$GTEST_CPPFLAGS])
  GTEST_LIBS=`echo $GTEST_LIBS | $CF_CUT -d ' ' -f 1`
  
  echo "    GTEST_CPPFLAGS: $GTEST_CPPFLAGS"
  echo "    GTEST_CXXFLAGS: $GTEST_CXXFLAGS"
  echo "    GTEST_LDFLAGS: $GTEST_LDFLAGS"
  echo "    GTEST_LIBS: $GTEST_LIBS"
  
  AC_SUBST([GTEST_CPPFLAGS])
  AC_SUBST([GTEST_CXXFLAGS])
  AC_SUBST([GTEST_LDFLAGS])
  AC_SUBST([GTEST_LIBS])
])