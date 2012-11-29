test_project_binname = test-project

bin_PROGRAMS = $(test_project_binname)
test_project_SOURCES = $(srcdir)/TestRunner.cpp $(srcdir)/TestClass.cpp

test_project_CPPFLAGS = $(GTEST_CPPFLAGS)
  
test_project_LDADD = $(GTEST_LIBS)