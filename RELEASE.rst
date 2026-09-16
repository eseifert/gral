Releasing to Maven Central
##########################

The version is derived from ``git describe``, so a release starts by tagging the
commit. Publishing then takes a single command, which uploads the artifacts to
the Central Portal, waits for them to be validated, and publishes them::

  $ git tag 0.16
  $ ./gradlew publish

All modules go into that one deployment, so a release never shows up in the
Portal as two: ``gral-core``, ``gral-swing``, ``gral-javafx``,
``gral-examples``, ``gral-javafx-examples`` and the aggregate ``gral``, which
the ``gral-all`` project publishes.

The build refuses to publish a version that does not come from a release tag.
Pass ``-PcentralPublishingType=USER_MANAGED`` to stop after validation and
release by hand at `<https://central.sonatype.com/publishing>`__ instead.

Five Gradle properties are expected, usually in ``~/.gradle/gradle.properties``.
The first two are the user token generated at
`<https://central.sonatype.com>`__, not the account password::

  ossrhUsername=<token username>
  ossrhPassword=<token password>
  signing.keyId=<last 8 or 16 digits of the GPG key id>
  signing.password=<passphrase of the GPG key>
  signing.secretKeyRingFile=<path to the exported secret keyring>

Maven Central requires signed artifacts, and GnuPG 2.1 and later no longer keep
the keyring file that Gradle expects. It has to be exported once with
``gpg --export-secret-keys <key id> > ~/.gnupg/secring.gpg``.
