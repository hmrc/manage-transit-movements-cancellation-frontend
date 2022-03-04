#!/bin/bash

echo ""
echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:departureId/$className;format="decap"$                  controllers.$className$Controller.onPageLoad(departureId: DepartureId, mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /:departureId/$className;format="decap"$                  controllers.$className$Controller.onSubmit(departureId: DepartureId, mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /:departureId/change$className$                        controllers.$className$Controller.onPageLoad(departureId: DepartureId, mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /:departureId/change$className$                        controllers.$className$Controller.onSubmit(departureId: DepartureId, mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "$className;format="decap"$.title = $className$" >> ../conf/messages.en
echo "$className;format="decap"$.heading = $className$" >> ../conf/messages.en
echo "$className;format="decap"$.hint = For example, 12 11 2007" >> ../conf/messages.en
echo "$className;format="decap"$.checkYourAnswersLabel = $className$" >> ../conf/messages.en
echo "$className;format="decap"$.error.required.all = Enter the $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.error.required.two = The $className;format="decap"$" must include {0} and {1} >> ../conf/messages.en
echo "$className;format="decap"$.error.required = The $className;format="decap"$ must include {0}" >> ../conf/messages.en
echo "$className;format="decap"$.error.invalid = Enter a real $className$" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/self: Generators =>/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrary$className$UserAnswersEntry: Arbitrary[($className$Page, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        value <- arbitrary[Int].map(Json.toJson(_))";\
    print "      } yield ($className$Page(DepartureId(1)), value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[($className$Page, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding to PageGenerators"
awk '/self: Generators =>/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrary$className$Page: Arbitrary[$className$Page] =";\
    print "    Arbitrary($className$Page(DepartureId(1)))";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def $className;format="decap"$: Option[Row] = userAnswers.get($className$Page) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"$className;format="decap"$.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(lit\"\${answer.format(utils.Format.dateFormatter)}"),\";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.$className$Controller.onPageLoad(departureId, CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"$className;format="decap"$.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration $className;format="snake"$ completed"
